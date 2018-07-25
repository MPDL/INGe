import { TestBed, inject } from '@angular/core/testing';

import { ElasticSearchService } from './elastic-search.service';

describe('ElasticSearchService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ElasticSearchService]
    });
  });

  it('should be created', inject([ElasticSearchService], (service: ElasticSearchService) => {
    expect(service).toBeTruthy();
  }));
});
