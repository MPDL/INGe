import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';

import { ElasticService } from '../service/elastic.service';
import { MessagesService } from '../../base/services/messages.service';


@Component({
  selector: 'app-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.scss']
})
export class IndexComponent implements OnInit {

  indices: any[];
  aliases: any;

  constructor(private elastic: ElasticService,
    private message: MessagesService,
    private route: ActivatedRoute,
    private router: Router, ) { }

  ngOnInit() {
    this.list();
    // this.listAliases();
  }

  async listAliases() {
    try {
      this.aliases = await this.elastic.listAliases();
    } catch (e) {
      this.message.error(e);
    }
  }

  async list() {
    try {
      this.indices = await this.elastic.listAllIndices();
      this.aliases = await this.elastic.listAliases();

      this.indices.sort((a, b) => {
        if (a.index < b.index) {
          return -1;
        } else if (a.index > b.index) {
          return 1;
        } else {
          return 0;
        }
      });

      this.indices.map(index => {
        index.alias = Object.keys(this.aliases[index.index].aliases);
      });
    } catch (e) {
      this.message.error(e);
    }
  }

  goTo(destination) {
    this.router.navigate(['elastic/index', destination]);
  }

  addNewIndex() {
    this.goTo('new');
  }

  async delete(index) {
    if (confirm('you\'re about 2 delete ' + index.index)) {
      try {
        const res = await this.elastic.delete(index.index);
        const pos = this.indices.indexOf(index);
        this.indices.splice(pos, 1);
        this.message.success('deleted ' + index.index + '\n' + JSON.stringify(res));
      } catch (e) {
        this.message.error(e);
      }
    }
  }
}
