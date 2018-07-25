import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { Subscription } from 'rxjs';

import { User, Grant, BasicRO } from '../../base/common/model';
import { UsersService } from '../services/users.service';
import { MessagesService } from '../../base/services/messages.service';
import { AuthenticationService } from '../../base/services/authentication.service';
import { environment } from '../../../environments/environment';
import { allOpenedOUs } from '../../base/common/query-bodies';

@Component({
  selector: 'app-user-details',
  templateUrl: './user-details.component.html',
  styleUrls: ['./user-details.component.scss']
})
export class UserDetailsComponent implements OnInit, OnDestroy {

  url = environment.rest_url + environment.rest_users;
  ous_url = environment.rest_url + environment.rest_ous;
  ctxs_url = environment.rest_url + environment.rest_contexts;

  resettedPassword: string = 'hard2Remember';
  selected: User;
  ous: any[];
  selectedOu: any;
  isNewUser: boolean = false;
  isNewGrant: boolean = false;
  isNewOu: boolean = false;
  isAdmin: boolean = true;
  grants2remove: boolean = false;
  selectedGrant: Grant;
  selectedGrants: Grant[] = [];
  grantsToRemove: string;
  ctxTitle: string;

  subscription: Subscription;
  token: string;
  tokenSubscription: Subscription;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private usersService: UsersService,
    private messageService: MessagesService,
    private loginService: AuthenticationService,
  ) { }

  ngOnInit() {
    this.tokenSubscription = this.loginService.token$.subscribe(token => {
      this.token = token;
    });

    this.selected = this.route.snapshot.data['user'];
    
    if (this.route.snapshot.queryParams['admin']) {
      this.isAdmin = this.route.snapshot.queryParams['admin'];
    }
    if (this.selected.loginname === 'new user') {
      this.isNewUser = true;
      this.isNewOu = true;
    }
    this.listOuNames();

  }

  listOuNames() {
    const body = allOpenedOUs;
    this.usersService.query(this.ous_url, null, body)
      .subscribe(ous => {
        this.ous = ous.list;
      });
  }

  onSelectOu(val) {
    this.selectedOu = val;
  }

  onChangeOu() {
    this.isNewOu = true;
    this.selected.affiliation = null;
  }

  ngOnDestroy() {
    this.tokenSubscription.unsubscribe();
  }

  addGrant() {
    this.isNewGrant = true;
  }

  deleteGrant(grant2delete) {
    this.grants2remove = true;
    this.selectedGrant = grant2delete;
    if (!this.selectedGrants.some(grant => (grant2delete.objectRef === grant.objectRef && grant2delete.role === grant.role))) {
      this.selectedGrants.push(grant2delete);
    }
    this.grantsToRemove = JSON.stringify(this.selectedGrants);
  }

  goToRef(grant) {
    this.selectedGrant = grant;
    const ref = this.selectedGrant.objectRef;
    if (ref === undefined) {
      this.messageService.warning('the reference of the selected grant is undefined!');
    } else {
      if (ref.startsWith('ou')) {
        this.router.navigate(['/organization', ref]);
      } else {
        if (ref.startsWith('ctx')) {
          this.router.navigate(['/context', ref]);
        }
      }
    }
  }

  viewRefTitle(grant) {
    const ref = grant.objectRef;
    if (ref === undefined) {
      this.ctxTitle = 'why do you point here?';
    } else {
      if (ref.startsWith('ou')) {
        this.usersService.get(this.ous_url, ref, null)
          .subscribe(ou => {
            this.ctxTitle = ou.metadata.name;
          });
      } else {
        if (ref.startsWith('ctx')) {
          this.usersService.get(this.ctxs_url, ref, null)
            .subscribe(ctx => {
              this.ctxTitle = ctx.name;
            });
        }
      }
    }
  }

  gotoList() {
    const userId = this.selected ? this.selected.loginname : null;
    this.router.navigate(['/users', { id: userId }]);
  }

  notAllowed(whatthehackever) {
    this.messageService.warning('you\'re not authorized !')
  }

  resetPassword(user) {
    user.password = this.resettedPassword;
    this.usersService.changePassword(user, this.token)
      .subscribe(u => {
        this.selected = u;
        this.messageService.warning(u.loginname + ':  password was reset to: ' + user.password);
      }, error => {
        this.messageService.error(error);
      });
  }

  changePassword(user) {
    if (user.password != null) {
      this.usersService.changePassword(user, this.token)
        .subscribe(u => {
          this.selected = u;
          this.messageService.warning(u.loginname + ':  password has changed to: ' + user.password);
        }, error => {
          this.messageService.error(error);
        });
    } else {
      this.messageService.error('password must not be empty!');
    }
  }

  activateUser(user) {
    this.selected = user;
    if (this.selected.active === true) {
      this.usersService.deactivate(this.selected, this.token)
        .subscribe(user2deactivate => {
          this.selected = user2deactivate;
          this.messageService.success('Deactivated ' + this.selected.objectId);
        }, error => {
          this.messageService.error(error);
        });
    } else {
      this.usersService.activate(this.selected, this.token)
        .subscribe(user2activate => {
          this.selected = user2activate;
          this.messageService.success('Activated ' + this.selected.objectId);
        }, error => {
          this.messageService.error(error);
        });
    }
  }

  save(user2save) {
    this.selected = user2save;
    if (this.selected.loginname.includes(' ')) {
      this.messageService.warning('loginname MUST NOT contain spaces');
      return;
    }
    if (this.selected.name == null) {
      this.messageService.warning('name MUST NOT be empty');
      return;
    }
    if (this.isNewUser) {
      if (this.selectedOu != null) {
        const ou_id = this.selectedOu.objectId;
        const aff = new BasicRO();
        aff.objectId = ou_id;
        this.selected.affiliation = aff;
      } else {
        this.messageService.warning('you MUST select an organization');
        return;
      }
      this.usersService.post(this.url, this.selected, this.token)
        .subscribe(
          data => {
            this.messageService.success('added new user ' + data);
            this.gotoList();
            this.selected = null;
          },
          error => {
            this.messageService.error(error);
          }
        );

    } else {
      if (this.isNewOu) {
        if (this.selectedOu != null) {
          const ou_id = this.selectedOu.objectId;
          const aff = new BasicRO();
          aff.objectId = ou_id;
          this.selected.affiliation = aff;
        } else {
          this.messageService.warning('you MUST select an organization');
          return;
        }
      }
      this.usersService.put(this.url + '/' + this.selected.objectId, this.selected, this.token)
        .subscribe(
          data => {
            this.messageService.success('updated ' + this.selected.loginname + ' ' + data);
            this.gotoList();
            this.selected = null;
            this.isNewGrant = false;
          },
          error => {
            this.messageService.error(error);
          }
        );
    }
  }

  removeGrants() {
    this.usersService.removeGrants(this.selected, this.selectedGrants, this.token).subscribe(user => {
      this.selected = user;
      if (this.selected.grantList) {
        this.selected.grantList.forEach(grant => this.usersService.addNamesOfGrantRefs(grant));
      }
      this.messageService.success('removed Grants from ' + this.selected.loginname);
      this.selectedGrants = null;
      this.grants2remove = false;
    }, error => {
      this.messageService.error(error);
    });
  }

}
