
import { throwError as observableThrowError, Observable } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams, HttpResponse } from '@angular/common/http';

import { MessagesService } from '../../base/services/messages.service';
@Injectable()
export class ConeService {

    answer;
    results: any[] = [];

    constructor(private http: HttpClient,
        private messages: MessagesService) {
    }

    getAllJournals(): Observable<any[]> {

        const journalUrl: string = 'http://b253.demo/blazegraph/namespace/inge/sparql';
        const headers = new HttpHeaders().set('Accept', 'application/sparql-results+json, application/json');
        const params = new HttpParams().set('query', 'select * {graph $g {$s $p $o}}');
        return this.http.request('GET', journalUrl, {
            headers: headers,
            params: params
        }).pipe(
            map((response: any) => {
                response.results.bindings.forEach(resource => {
                    this.results.push(resource);
                });
                return this.results;
            })
        );
    }

    private handleError(error: any) {
    console.error(error);
    return observableThrowError(error.json().error || ' error');
  }

}
