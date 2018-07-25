import { Component, OnInit } from '@angular/core';
import { PaginationService } from '../services/pagination.service';

@Component({
  selector: 'pagination-component',
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.scss']
})
export class PaginationComponent implements OnInit {

  paginator: any = {};
  pagedItems: any[];
  itemList: any[];

  constructor(private pagination: PaginationService) { }

  ngOnInit() {
  }

  init(page: number, list: any[]) {
    this.itemList = list;
    if (page < 1 || page > this.paginator.totalPages) {
      return;
    }

    this.paginator = this.pagination.getPaginator(this.itemList.length, page);

    this.pagedItems = this.itemList.slice(this.paginator.startIndex, this.paginator.endIndex + 1);
  }

}
