import { Component, OnInit } from '@angular/core';
const { version: appVersion } = require('../../../../package.json');
const { homepage: appHome } = require('../../../../package.json');

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss']
})
export class FooterComponent implements OnInit {

  appVersion;
  appName = 'Pubman Administration';
  appHome;

  constructor() { }

  ngOnInit() {
    this.appVersion = appVersion;
    this.appHome = appHome;
  }

}
