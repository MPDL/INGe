import { TestBed, inject } from '@angular/core/testing';

import { ContextDetailsResolverService } from './context-details-resolver.service';

describe('ContextDetailsResolverService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ContextDetailsResolverService]
    });
  });

  it('should be created', inject([ContextDetailsResolverService], (service: ContextDetailsResolverService) => {
    expect(service).toBeTruthy();
  }));
});
