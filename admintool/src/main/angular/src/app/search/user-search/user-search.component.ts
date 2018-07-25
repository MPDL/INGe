import { Component, OnInit, OnDestroy, AfterViewInit, QueryList, ViewChild, ViewChildren } from '@angular/core';
import { Validators, FormGroup, FormArray, FormBuilder } from '@angular/forms';
import { Router, ActivatedRoute, Params } from '@angular/router';

import { Observable, Subscription } from 'rxjs';
import * as bodyBuilder from 'bodybuilder';

import { MessagesService } from '../../base/services/messages.service';
import { AuthenticationService } from '../../base/services/authentication.service';
import { ElasticSearchService } from '../services/elastic-search.service';
import { SearchService } from '../services/search.service';
import { SearchTermComponent } from '../search-term/search-term.component';
import { SearchRequest, SearchTerm } from '../search-term/search.term';
import { user_aggs } from '../search-term/search.aggregations';

import { environment } from '../../../environments/environment';


@Component({
  selector: 'app-user-search',
  templateUrl: './user-search.component.html',
  styleUrls: ['./user-search.component.scss']
})
export class UserSearchComponent implements OnInit, OnDestroy {

  @ViewChildren(SearchTermComponent) components: QueryList<SearchTermComponent>;
  url = environment.rest_url + environment.rest_users;
  searchForm: FormGroup;
  searchRequest: SearchRequest;

  searchTerm: string;
  selectedField: string;
  fields2Select: string[] = [];
  aggregationsList: any[] = [];
  selectedAggregation: any;
  years: any[] = [];
  ous: Array<any>;
  publishers: Array<any>;
  selected;
  users: any[];
  total: number = 0;
  loading: boolean = false;
  pageSize: number = 25;
  currentPage: number = 1;
  subscription: Subscription;
  token;
  index: string = 'default';

  constructor(private elastic: ElasticSearchService,
    private search: SearchService,
    private message: MessagesService,
    private login: AuthenticationService,
    private builder: FormBuilder,
    private router: Router) { }

  get diagnostic() { return JSON.stringify(this.years); }

  ngOnInit() {
    for (const agg in user_aggs) {
      this.aggregationsList.push(agg);
    }
    this.fields2Select = this.elastic.getMappingFields(environment.user_index.name, environment.user_index.type);
    this.subscription = this.login.token$.subscribe(token => {
      this.token = token;
    });
    this.searchForm = this.builder.group({
      searchTerms: this.builder.array([this.initSearchTerm()])
    });
  }

  get searchTerms(): FormArray {
    return this.searchForm.get('searchTerms') as FormArray;
  }

  initSearchTerm() {
    return this.builder.group({
      type: '',
      field: '',
      searchTerm: '',
      fields: []
    });
  }

  addSearchTerm() {
    this.searchTerms.push(this.initSearchTerm());
  }

  removeSearchTerm(i: number) {
    this.searchTerms.removeAt(i);
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  onAggregationSelect(agg) {
    this.selectedAggregation = user_aggs[agg];
    switch (agg) {
      case 'creationDate':
        this.years = this.elastic.buckets(environment.user_index.name, this.selectedAggregation, false);
        this.selected = agg;
        break;
      case 'organization':
        this.ous = this.elastic.buckets(environment.user_index.name, this.selectedAggregation, false);
        this.selected = agg;
        break;
      default:
        this.selected = null;
    }
  }

  getPage(page: number) {
    this.searchRequest = this.prepareRequest();
    const body = this.search.buildQuery(this.searchRequest, 25, ((page - 1) * 25), 'name.keyword', 'asc');
    this.loading = true;
    this.search.query(this.url, this.token, body)
      .subscribe(res => {
        this.total = res.records;
        this.currentPage = page;
        this.users = res.list
        this.loading = false;
      }, (err) => {
        this.message.error(err);
      });
  }

  searchItems(body) {
    if (this.token !== null) {
      this.currentPage = 1;
      this.search.query(this.url, this.token, body)
        .subscribe(res => {
          this.users = res.list;
          this.total = res.records;
        }, err => {
          this.message.error(err);
        });
    } else {
      this.message.warning('no login, no users!');
    }
  }

  onSelectYear(year) {
    if (this.token !== null) {
      this.searchForm.reset();
      this.searchForm.controls.searchTerms.patchValue([{ type: 'filter', field: 'creationDate', searchTerm: year.key_as_string + '||/y' }]);
      this.currentPage = 1;
      this.search.filter(this.url, this.token, '?q=creationDate:' + year.key + '||/y', 1)
        .subscribe(res => {
          this.users = res.list;
          this.total = res.records;
        }, err => {
          this.message.error(err);
        });
    } else {
      this.message.warning('no login, no users!');
    }
  }

  onSelectOu(ou) {
    if (this.token !== null) {
      this.searchForm.reset();
      this.searchForm.controls.searchTerms.patchValue([{ type: 'filter', field: 'affiliation.name.keyword', searchTerm: ou.key }]);
      this.currentPage = 1;
      this.search.filter(this.url, this.token, '?q=affiliation.name.keyword:' + ou.key, 1)
        .subscribe(res => {
          this.users = res.list;
          this.total = res.records;
        }, err => {
          this.message.error(err);
        });
    } else {
      this.message.warning('no login, no users!');
    }
  }

  onSelect(item) {
    if (confirm('wanna edit it?')) {
      this.router.navigate(['/user', item.objectId], { queryParams: { token: this.token }, skipLocationChange: true });
    }
  }

  handleNotification(event: string, index) {
    if (event === 'add') {
      this.addSearchTerm();
    } else if (event === 'remove') {
      this.removeSearchTerm(index);
    }
  }

  submit() {
    this.searchRequest = this.prepareRequest();
    const preparedBody = this.search.buildQuery(this.searchRequest, 25, 0, 'name.keyword', 'asc');
    this.searchItems(preparedBody);
  }

  prepareRequest(): SearchRequest {
    const model = this.searchForm.value;
    const searchTerms2Save: SearchTerm[] = model.searchTerms.map(
      (term: SearchTerm) => Object.assign({}, term)
    );
    const request: SearchRequest = {
      searchTerms: searchTerms2Save
    };
    return request;
  }

}
