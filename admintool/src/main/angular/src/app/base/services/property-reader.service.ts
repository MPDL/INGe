
import {of as observableOf,  Observable, Subscription, BehaviorSubject } from 'rxjs';

import {map} from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';



@Injectable()
export class PropertyReaderService {

  propertyFileUrl = './assets/properties.json';
  public props: Object;
  private propSubject: BehaviorSubject<Object> = new BehaviorSubject<Object>(null);

  constructor(private http: HttpClient) { }

  readPropertyFile() {
    if (this.props === null || this.props === undefined) {
      return this.http.get<Object>(this.propertyFileUrl).pipe(
        map(props => this.props = props));
    }
    return observableOf(null);
  }

  get(key: any) {
    return this.props[key];
  }

  setProperties(props: Object) {
    if (props === null || props === undefined) {
      return;
    }
    this.props = props;
    if (this.propSubject) {
      this.propSubject.next(this.props);
    }
  }

  public subscribe(caller: any, callback: (caller: any, props: Object) => void) {
    this.propSubject
      .subscribe((props) => {
        if (props === null) {
          return;
        }
        callback(caller, props);
      });
  }

  public subscribe2(callback) {
    this.propSubject
      .subscribe((props) => {
        if (props === null) {
          return;
        }
        callback(props);
      });
  }

}
