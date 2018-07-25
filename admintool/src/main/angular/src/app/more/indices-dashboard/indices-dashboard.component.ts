import { Component, OnInit } from '@angular/core';

import { IndicesService } from '../indices-services/indices.service';

@Component({
  // selector: 'app-indices-dashboard',
  templateUrl: './indices-dashboard.component.html',
  styleUrls: ['./indices-dashboard.component.scss']
})
export class IndicesDashboardComponent implements OnInit {

  info: any;
  constructor(private svc: IndicesService) { }

  ngOnInit() {
    this.svc.localNodeInfo(info => {
      this.info = info;
    });
  }

}
