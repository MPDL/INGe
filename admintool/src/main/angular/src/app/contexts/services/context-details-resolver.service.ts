
import { throwError as observableThrowError, Observable, of } from 'rxjs';
import { first, catchError } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { Router, Resolve, RouterStateSnapshot, ActivatedRouteSnapshot } from '@angular/router';


import { ContextsService } from './contexts.service';
import { MessagesService } from '../../base/services/messages.service';
import { Context } from '../../base/common/model';
import { environment } from '../../../environments/environment';

@Injectable()
export class ContextDetailsResolverService implements Resolve<any> {

    constructor(
        private ctxSvc: ContextsService,
        private message: MessagesService,
        private router: Router) { }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Context> {
        const url = environment.rest_url + environment.rest_contexts;
        const id = route.params['id'];
        if (id === 'new ctx') {
            const ctx = new Context();
            ctx.name = 'new ctx';
            ctx.responsibleAffiliations = [];
            ctx.allowedGenres = [];
            ctx.allowedSubjectClassifications = [];
            ctx.workflow = 'STANDARD';
            return of(ctx);
        } else {
            const token = route.params['token'];
            return this.ctxSvc.get(url, id, token)
                .pipe(
                    first(),
                    catchError((err, obs) => {
                        this.message.error(err);
                        return observableThrowError(err);
                    })
                );
        }
    }

}

