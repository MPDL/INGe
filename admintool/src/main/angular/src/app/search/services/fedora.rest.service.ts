
import { throwError as observableThrowError, Observable } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';

@Injectable()
export class FedoraRestService {

  fcrepo_rest_url = 'http://localhost:8888/fcrepo/rest';

  constructor(protected http: HttpClient) { }

  getResource(): Observable<any> {
    const headers = new HttpHeaders().set('Accept', 'application/ld+json');
    const url = this.fcrepo_rest_url + '/objects/raven';
    return this.http.request('GET', url, {
      headers: headers
    }).pipe(
      map((response: Response) => {
        const data = response.json();
        return data;
      }),
      catchError((error: any) => observableThrowError(error))
    );
  }
}
