import { Injectable } from '@angular/core';

@Injectable()
export class PaginationService {

  constructor() { }

  getPaginator(totalItems: number, currentPage: number = 1, pageSize: number = 25) {
    const totalPages = Math.ceil(totalItems / pageSize);

    let startPage: number, endPage: number;
    if (totalPages <= 10) {
      startPage = 1;
      endPage = totalPages;
    } else {
      if (currentPage <= 6) {
        startPage = 1;
        endPage = 10;
      } else if (currentPage + 4 >= totalPages) {
        startPage = totalPages - 9;
        endPage = totalPages;
      } else {
        startPage = currentPage - 5;
        endPage = currentPage + 4;
      }
    }

    const startIndex = (currentPage - 1) * pageSize;
    const endIndex = Math.min(startIndex + pageSize - 1, totalItems - 1);

    const pages = this.getPages(startPage, endPage + 1, 1);

    return {
      totalItems: totalItems,
      currentPage: currentPage,
      pageSize: pageSize,
      totalPages: totalPages,
      startPage: startPage,
      endPage: endPage,
      startIndex: startIndex,
      endIndex: endIndex,
      pages: pages
    };
  }

  getPages(start: number, stop: number, step: number) {
    if (stop == null) {
      stop = start || 0;
      start = 0;
    }
    step = step || 1;

    const length = Math.max(Math.ceil((stop - start) / step), 0);
    const range = Array(length);

    for (let idx = 0; idx < length; idx++ , start += step) {
      range[idx] = start;
    }

    return range;
  }
}

