
import {catchError, map} from 'rxjs/operators';

import {throwError as observableThrowError,  Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { Router, Resolve, RouterStateSnapshot,
  ActivatedRouteSnapshot } from '@angular/router';

import { PropertyReaderService } from './property-reader.service';

@Injectable()
export class PropertyResolverService implements Resolve<Object> {

  constructor(private router: Router, private reader: PropertyReaderService) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Object> {
    return this.reader.readPropertyFile().pipe(
      map(props => {
        this.reader.setProperties(props);
        return this.reader.props;
      }), catchError((error, observable) => {
        console.log(error);
        return observableThrowError(error);
      }));
  }



}
