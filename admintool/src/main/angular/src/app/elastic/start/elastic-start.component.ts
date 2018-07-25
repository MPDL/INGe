import { Component, OnInit } from '@angular/core';
import { ElasticService } from '../service/elastic.service';
import { MessagesService } from '../../base/services/messages.service';

@Component({
  selector: 'app-elastic-start',
  templateUrl: './elastic-start.component.html',
  styleUrls: ['./elastic-start.component.scss']
})
export class ElasticStartComponent implements OnInit {

  info: any;
  host: any;

  constructor(private elastic: ElasticService,
    private message: MessagesService) { }

  ngOnInit() {
    this.getInfo();
  }

  async getInfo() {
    try {
      this.info = await this.elastic.info_api();
    } catch (e) {
      this.message.error(e);
    }
  }

  connect2(server) {
    this.host = server;
    this.elastic.connect2(this.host);
    this.getInfo();
  }
}
