import { TestBed, inject } from '@angular/core/testing';

import { ContextsService } from './contexts.service';

describe('ContextsService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ContextsService]
    });
  });

  it('should be created', inject([ContextsService], (service: ContextsService) => {
    expect(service).toBeTruthy();
  }));
});
