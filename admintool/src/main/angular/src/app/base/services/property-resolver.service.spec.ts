import { TestBed, inject } from '@angular/core/testing';

import { PropertyResolverService } from './property-resolver.service';

describe('PropertyResolverService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PropertyResolverService]
    });
  });

  it('should be created', inject([PropertyResolverService], (service: PropertyResolverService) => {
    expect(service).toBeTruthy();
  }));
});
