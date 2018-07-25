import { Component, OnInit } from '@angular/core';

import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  host: string = environment.base_url;

  constructor() { }

  ngOnInit() {
  }

  theDeveloperSays() {
    alert('Your suggestion will be sent ...');
  }

  info() {
    alert('NOT implemented yet ...');
  }

}
