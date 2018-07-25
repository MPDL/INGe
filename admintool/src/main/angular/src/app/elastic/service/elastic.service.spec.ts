import { TestBed, inject } from '@angular/core/testing';

import { ElasticService } from './elastic.service';

describe('ElasticService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ElasticService]
    });
  });

  it('should be created', inject([ElasticService], (service: ElasticService) => {
    expect(service).toBeTruthy();
  }));
});
