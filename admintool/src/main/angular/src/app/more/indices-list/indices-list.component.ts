import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';

import { MessagesService } from '../../base/services/messages.service';
import { IndicesService } from '../indices-services/indices.service';

@Component({
  selector: 'app-indices-list',
  templateUrl: './indices-list.component.html',
  styleUrls: ['./indices-list.component.scss']
})
export class IndicesListComponent implements OnInit {

  indices: any[];

  constructor(private service: IndicesService,
    private route: ActivatedRoute,
    private router: Router,
    private message: MessagesService) { }

  ngOnInit() {
    this.service.listAllIndices(indices => {
      this.indices = indices;
    });
  }

  goTo(destination) {
    this.router.navigate(['more/list', destination]);

  }

  delete(index) {
    if (confirm('you\'re about 2 delete ' + index.index)) {
      this.service.delete(index.index, deleted => {
        const pos = this.indices.indexOf(index);
        this.indices.splice(pos, 1);
        this.message.success('deleted ' + JSON.stringify(deleted));
      });
    }
  }

  addNewIndex() {
    this.goTo('new');
  }

}
