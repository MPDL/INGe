import { Component, OnInit } from '@angular/core';

import { MessagesService } from '../services/messages.service';

@Component({
  selector: 'messages-component',
  templateUrl: './messages.component.html',
  styleUrls: ['./messages.component.scss']
})
export class MessagesComponent implements OnInit {

  message: any;

  constructor(private messages: MessagesService) { }

  ngOnInit() {
     this.messages.getMessage().subscribe(message => { this.message = message; });
  }

  delete(message) {
    this.message = null;
  }

}
