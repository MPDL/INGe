import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';

import { PaginationComponent } from '../../base/pagination/pagination.component';

import { MessagesService } from '../../base/services/messages.service';
import { AuthenticationService } from '../../base/services/authentication.service';
import { ContextsService } from '../services/contexts.service';
import { environment } from '../../../environments/environment';
import { mpgOus4auto } from '../../base/common/query-bodies';

@Component({
  selector: 'app-context-list',
  templateUrl: './context-list.component.html',
  styleUrls: ['./context-list.component.scss']
})
export class ContextListComponent implements OnInit, OnDestroy {

  @ViewChild(PaginationComponent)
  private paginator: PaginationComponent;
  url = environment.rest_url + environment.rest_contexts;
  title: string = 'Contexts';
  ctxs: any[];
  contextnames: any[] = [];
  contextSearchTerm;
  ounames: any[] = [];
  ouSearchTerm;
  selectedOUName;
  selected;
  token;
  subscription: Subscription;
  pagedCtxs: any[];
  total: number = 1;
  loading: boolean = false;
  pageSize: number = 25;
  currentPage: number = 1;

  constructor(private ctxSvc: ContextsService,
    private router: Router,
    private message: MessagesService,
    private loginService: AuthenticationService) { }

  ngOnInit() {
    this.subscription = this.loginService.token$.subscribe(token => {
      this.token = token;
    });
    this.listAllContexts(this.token);
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  getPage(page: number) {
    this.loading = true;
    this.ctxSvc.getAll(this.url, this.token, page)
      .subscribe(result => {
        this.ctxs = result.list;
        this.total = result.records;
      }, (err) => {
        this.message.error(err);
      });
    this.currentPage = page;
    this.loading = false;

  }

  updatePaginator() {
    this.pagedCtxs = this.paginator.pagedItems;
  }

  listAllContexts(token) {
    this.ctxSvc.getAll(this.url, this.token, 1)
      .subscribe(ctxs => {
        this.ctxs = ctxs.list;
        this.total = ctxs.records;
      });
  }

  goTo(ctx) {
    const id = ctx.objectId;
    this.router.navigate(['/context', id]);
  }

  isSelected(ctx) {
    this.selected = ctx;
    return ctx.objectId === this.selected.objectId;
  }

  addNewContext() {
    const ctxid = 'new ctx';
    this.router.navigate(['/context', ctxid]);
  }

  getContextNames(term: string) {
    if (term.length > 0 && !term.startsWith('"')) {
      this.returnSuggestedContexts(term);
    } else if (term.length > 3 && term.startsWith('"') && term.endsWith('"')) {
      this.returnSuggestedContexts(term);
    }
  }

  returnSuggestedContexts(term) {
    const contextNames: any[] = [];
    const queryString = '?q=name.auto:' + term;
    this.ctxSvc.filter(this.url, null, queryString, 1)
      .subscribe(res => {
        res.list.forEach(ctx => {
          contextNames.push(ctx);
        });
        if (contextNames.length > 0) {
          this.contextnames = contextNames;
        } else {
          this.contextnames = [];
        }
      }, err => {
        this.message.error(err);
      });
  }

  getOUNames(term: string) {
    const ouNames: any[] = [];
    if (term.length > 0) {
      const body = mpgOus4auto;
      body.query.bool.must.term["metadata.name.auto"] = term;
      const url = environment.rest_url + environment.rest_ous;
      this.ctxSvc.query(url, null, body)
        .subscribe(res => {
          res.list.forEach(ou => {
            ouNames.push(ou);
          });
          if (ouNames.length > 0) {
            this.ounames = ouNames;
          } else {
            this.ounames = [];
          }
        }, err => {
          this.message.error(err);
        });
    }
  }

  filter(ou) {
    this.selectedOUName = ou;
    this.currentPage = 1;
    this.ctxSvc.filter(this.url, null, '?q=responsibleAffiliations.objectId:' + ou.objectId, 1)
      .subscribe(res => {
        this.ctxs = res.list;
        if (res.records > 0) {
          this.total = res.records;
        } else {
          this.message.info('query did not return any results.')
        }
      }, err => {
        this.message.error(JSON.stringify(err));
      });
    this.title = 'Contexts for ' + this.selectedOUName.name;
    this.closeOUNames();
  }

  close() {
    this.contextSearchTerm = '';
    this.contextnames = [];
  }

  closeOUNames() {
    this.ouSearchTerm = '';
    this.ounames = [];
  }

  select(term) {
    this.contextSearchTerm = term.name;
    this.router.navigate(['/context', term.objectId]);
    this.contextnames = [];
  }

  delete(ctx) {
    alert('deleting ' + ctx.name + ' not yet implemented');
  }
}
