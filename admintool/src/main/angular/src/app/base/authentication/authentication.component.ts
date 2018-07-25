import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { MessagesService } from '../services/messages.service';
import { AuthenticationService } from '../services/authentication.service';

import { User } from '../common/model';

@Component({
  selector: 'authentication-component',
  templateUrl: './authentication.component.html',
  styleUrls: ['./authentication.component.scss']
})
export class AuthenticationComponent implements OnInit {

  credentials: any = {};
  loggedIn = false;
  hasToken = false;
  empty = true;
  token = '';
  user: User;

  constructor(
    private router: Router,
    private messages: MessagesService,
    private authentication: AuthenticationService
  ) { }

  ngOnInit() {

  }

  login() {
    this.authentication.login(this.credentials.username, this.credentials.password)
      .subscribe(
      data => {
        this.token = data;
        this.who();
        this.loggedIn = true;
      },
      error => {
        this.messages.error(error);
        this.loggedIn = false;
      }
      );
  }

  logout() {
    this.authentication.logout();
    this.credentials.username = '';
    this.credentials.password = '';
    this.loggedIn = false;
    // required because ngForm cannot be reset.
    // restore 'pristine' class state.
    this.empty = false;
    setTimeout(() => this.empty = true, 0);
  }

  who() {
    if (this.token !== '') {
      this.authentication.who(this.token)
        .subscribe(
        data => {
          this.user = data;
          this.hasToken = true;

          this.credentials.username = this.user.name;
        },
        error => {
          this.messages.error(error);
          this.hasToken = false;
        }
        );
    }
  }

}
