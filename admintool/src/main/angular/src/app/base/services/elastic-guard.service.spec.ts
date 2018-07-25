import { TestBed, inject } from '@angular/core/testing';

import { ElasticGuard } from './elastic-guard.service';

describe('ElasticGuardService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ElasticGuard]
    });
  });

  it('should be created', inject([ElasticGuard], (service: ElasticGuard) => {
    expect(service).toBeTruthy();
  }));
});
