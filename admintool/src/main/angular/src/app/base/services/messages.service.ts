import { Injectable } from '@angular/core';
import { Router, NavigationStart } from '@angular/router';
import { Observable , Subject } from 'rxjs';

@Injectable()
export class MessagesService {

  private subject = new Subject<any>();
  private keepMessage = false;

  constructor(private router: Router) {
    router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        if (this.keepMessage) {
          this.keepMessage = false;
        } else {
          this.subject.next();
        }
      }
    });
  }

  info(message: string, keepMessage = false) {
    this.keepMessage = keepMessage;
    this.subject.next({ text: message });
  }

  success(message: string, keepMessage = false) {
    this.keepMessage = keepMessage;
    this.subject.next({ type: 'success', text: message });
  }

  warning(message: string, keepMessage = false) {
    this.keepMessage = keepMessage;
    this.subject.next({ type: 'warning', text: message });
  }

  error(message: string, keepMessage = false) {
    this.keepMessage = keepMessage;
    this.subject.next({ type: 'error', text: message });
  }

  getMessage(): Observable<any> {
    return this.subject.asObservable();
  }

}
