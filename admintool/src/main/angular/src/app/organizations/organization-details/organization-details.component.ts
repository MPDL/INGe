import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { Subscription } from 'rxjs';

import { AuthenticationService } from '../../base/services/authentication.service';
import { MessagesService } from '../../base/services/messages.service';
import { OrganizationsService } from '../services/organizations.service';
import { OU, Identifier, BasicRO, UserRO, OUMetadata } from '../../base/common/model';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-organization-details',
  templateUrl: './organization-details.component.html',
  styleUrls: ['./organization-details.component.scss']
})
export class OrganizationDetailsComponent implements OnInit, OnDestroy {

  ou_rest_url = environment.rest_url + environment.rest_ous;
  token: string;
  selected: OU;
  children: OU[];
  predecessors: OU[] = [];
  alternativeName;
  description;
  ouIdentifierId;

  subscription: Subscription;
  loginSubscription: Subscription;
  isNewOrganization: boolean = false;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private ouSvc: OrganizationsService,
    private login: AuthenticationService,
    private message: MessagesService
  ) { }

  ngOnInit() {
    this.subscription = this.route.params
      .subscribe(params => {
        this.loginSubscription = this.login.token$.subscribe(token => {
          this.token = token;
        });
        const id = params['id'];
        if (id === 'new org') {
          this.isNewOrganization = true;
          this.selected = this.prepareNewOU(id);

        } else {
          this.getSelectedOu(id, this.token);
          this.listChildren(id);
        }
      });
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
    this.loginSubscription.unsubscribe();
  }

  getSelectedOu(id, token) {
    this.ouSvc.get(this.ou_rest_url, id, token)
      .subscribe(ou => {
        this.selected = ou;
        if (this.selected.hasPredecessors === true) {
          const pre_id = this.selected.predecessorAffiliations[0].objectId;
          this.listPredecessors(pre_id, token);
        } else {
          this.predecessors = [];
        }
      }, error => {
        this.message.error(error);
      });
  }

  listPredecessors(id: string, token) {
    const query = '?q=objectId:' + id;
    this.ouSvc.filter(this.ou_rest_url, token, query, 1)
      .subscribe(ous => {
        this.predecessors = ous.list;
      });
  }

  listChildren(mother: string) {
    this.ouSvc.listChildren4Ou(mother, null)
      .subscribe(children => {
        this.children = children;
      });
  }

  openClose(ou) {
    this.selected = ou;
    if (this.selected.publicStatus === 'CREATED' || this.selected.publicStatus === 'CLOSED') {
      this.ouSvc.openOu(this.selected, this.token)
        .subscribe(httpStatus => {
          this.getSelectedOu(this.selected.objectId, this.token);
          this.message.success('Opened ' + this.selected.objectId + ' ' + httpStatus);
        }, error => {
          this.message.error(error);
        });
    } else {
      this.ouSvc.closeOu(this.selected, this.token)
        .subscribe(httpStatus => {
          this.getSelectedOu(this.selected.objectId, this.token);
          this.message.success('Closed ' + this.selected.objectId + ' ' + httpStatus);
        }, error => {
          this.message.error(error);
        });
    }
  }

  isSelected(name) {
    return true;
  }

  addName(selected) {

    if (selected != null && selected !== '') {

      if (this.selected.metadata.alternativeNames) {
        if (!this.selected.metadata.alternativeNames.includes(selected)) {
          this.selected.metadata.alternativeNames.push(selected);
        }
      } else {
        this.selected.metadata.alternativeNames = [];
        this.selected.metadata.alternativeNames.push(selected);
      }
    }

    this.alternativeName = '';
  }

  deleteName(selected) {
    const index = this.selected.metadata.alternativeNames.indexOf(selected);
    this.selected.metadata.alternativeNames.splice(index, 1);
  }

  clearNames() {
    this.selected.metadata.alternativeNames.splice(0, this.selected.metadata.alternativeNames.length);
  }

  addDesc(selected) {
    if (selected != null && selected !== '') {

      if (this.selected.metadata.descriptions) {
        if (!this.selected.metadata.descriptions.includes(selected)) {
          this.selected.metadata.descriptions.push(selected);
        }
      } else {
        this.selected.metadata.descriptions = [];
        this.selected.metadata.descriptions.push(selected);
      }
    }

    this.description = '';
  }

  deleteDesc(selected) {
    const index = this.selected.metadata.descriptions.indexOf(selected);
    this.selected.metadata.descriptions.splice(index, 1);
  }

  clearDescs() {
    this.selected.metadata.descriptions.splice(0, this.selected.metadata.descriptions.length);
  }

  addIdentifier(selected) {
    if (selected != null && selected !== '') {

      const ouid = new Identifier();
      ouid.id = selected;
      if (this.selected.metadata.identifiers) {
        if (!this.selected.metadata.identifiers.some(id => (id.id === selected))) {
          this.selected.metadata.identifiers.push(ouid);
        }
      } else {
        this.selected.metadata.identifiers = [];
        this.selected.metadata.identifiers.push(ouid);
      }
    }
    this.ouIdentifierId = '';
  }

  deleteIdentifier(selected) {
    const index = this.selected.metadata.identifiers.indexOf(selected);
    this.selected.metadata.identifiers.splice(index, 1);
  }

  clearIdentifiers() {
    this.selected.metadata.identifiers.splice(0, this.selected.metadata.identifiers.length);
  }

  save(ou) {
    this.selected = ou;
    if (this.selected.parentAffiliation.objectId === '') {
      this.message.warning('parent id MUST NOT be empty');
      return;
    }
    if (this.selected.metadata.name.includes('new ou')) {
      this.message.warning('name MUST NOT be new ou');
      return;
    }
    if (this.isNewOrganization) {
      this.ouSvc.post(this.ou_rest_url, this.selected, this.token)
        .subscribe(
        data => {
          this.message.success('added new organization ' + data);
          this.gotoList();
          this.selected = null;
        },
        error => {
          this.message.error(error);
        });

    } else {
      this.message.success('updating ' + this.selected.objectId);
      this.ouSvc.put(this.ou_rest_url + '/' + this.selected.objectId, this.selected, this.token)
        .subscribe(
        data => {
          this.message.success('updated ' + this.selected.objectId + ' ' + data);
          this.gotoList();
          this.selected = null;
        },
        error => {
          this.message.error(error);
        }
        );
    }
  }

  delete(ou) {
    this.selected = ou;
    const id = this.selected.objectId;
    this.ouSvc.delete(this.ou_rest_url + '/' + this.selected.objectId, this.selected, this.token)
      .subscribe(
      data => {
        this.message.success('deleted ' + id + ' ' + data);
      }, error => {
        this.message.error(error);
      });
    this.gotoList();
  }


  showDetails(id) {
    this.router.navigate(['/organization', id]);
  }

  gotoList() {
    this.router.navigate(['/organizations']);
  }

  get diagnostic() { return JSON.stringify(this.selected); }

  prepareNewOU(id): OU {
    const template = new OU();
    const creator = new UserRO();
    creator.objectId = '';
    template.creator = creator;
    const parent = new BasicRO();
    parent.objectId = '';
    template.parentAffiliation = parent;
    const meta = new OUMetadata();
    meta.name = 'new ou';
    template.metadata = meta;
    return template;

  }

}
