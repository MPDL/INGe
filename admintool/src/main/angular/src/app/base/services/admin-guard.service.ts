import { Injectable, OnDestroy } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot, CanActivateChild } from '@angular/router';
import { Subscription } from 'rxjs';

import { AuthenticationService } from './authentication.service';
import { MessagesService } from './messages.service';

@Injectable()
export class AdminGuard implements CanActivate, CanActivateChild, OnDestroy {

    checked: boolean = false;
    subscription: Subscription;

    constructor(private authentication: AuthenticationService,
        private router: Router, private message: MessagesService) { }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
        const url: string = state.url;
        return this.checkLogin(url);
    }

    canActivateChild(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
        return this.canActivate(route, state);
    }

    checkLogin(url: string): boolean {
        this.subscription = this.authentication.isAdmin$.subscribe(bool => {
            this.checked = bool;
        });
        if (this.checked) {
            return true;
        }
        // this.router.navigate(['/home']);
        this.message.warning('This site requires admin authorization ...')
        return false;
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }
}
