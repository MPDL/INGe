import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { Observable ,  Subscription } from 'rxjs';

import { AuthenticationService } from '../../base/services/authentication.service';
import { MessagesService } from '../../base/services/messages.service';
import { OrganizationsService } from '../services/organizations.service';
import { Elastic4ousService } from '../services/elastic4ous.service';

@Component({
  selector: 'app-organization-list',
  templateUrl: './organization-list.component.html',
  styleUrls: ['./organization-list.component.scss']
})
export class OrganizationListComponent implements OnInit, OnDestroy {

  external: boolean = false;
  current: string = '';
  currentChild = '';
  selected: any;
  searchTerm;
  ounames: any[] = [];
  subscription: Subscription;
  token;
  mpgOus: Observable<any[]>;
  extOus: Observable<any[]>;
  children: Observable<any[]>;
  grandChildren: Observable<any[]>;
  grandGrandChildren: any[];

  constructor(
    private ouSvc: OrganizationsService,
    private elastic: Elastic4ousService,
    private router: Router,
    private route: ActivatedRoute,
    private message: MessagesService,
    private loginService: AuthenticationService
  ) { }

  ngOnInit() {
    this.subscription = this.loginService.token$.subscribe(token => {
      this.token = token;
    });
    this.listOuNames(this.token);
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  listOuNames(token) {
    this.mpgOus = this.ouSvc.listChildren4Ou('ou_persistent13', token);
    this.extOus = this.ouSvc.listChildren4Ou('ou_persistent22', token);
  }

  getChildren(id) {
    this.current = id;
    this.children = this.ouSvc.listChildren4Ou(id, this.token);
  }

  getRidOfChildren(id) {
    this.current = '';
    this.children = null;
    if (this.currentChild.length > 0) {
      this.currentChild = '';
      this.grandChildren = null;
    }
  }

  getChildrenOfChild(id) {
    this.currentChild = id;
    this.grandChildren = this.ouSvc.listChildren4Ou(id, this.token);
  }

  getRidOfChildrenOfChild(id) {
    this.currentChild = '';
    this.grandChildren = null;
  }

  addNewOrganization() {
    const id = 'new org';
    this.router.navigate(['/organization', id]);
  }

  onSelect(ou: any) {
    const id: string = ou.objectId;
    this.router.navigate(['/organization', id]);
  }

  isSelected(ou) {
    return true;
  }

  getNames(a) {
    const ouNames: any[] = [];
    this.elastic.ous4auto(a, (names) => {
      names.forEach(name => ouNames.push(name));
      if (ouNames.length > 0) {
        this.ounames = ouNames;
      } else {
        this.ounames = [];
      }
    });
  }

  close() {
    this.searchTerm = '';
    this.ounames = [];
  }

  select(term) {
    this.searchTerm = term.metadata.name;
    this.router.navigate(['/organization', term.objectId]);
    this.ounames = [];
  }

}
