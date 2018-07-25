import { TestBed, inject } from '@angular/core/testing';

import { OrganizationDetailsResolverService } from './organization-details-resolver.service';

describe('OrganizationDetailsResolverService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [OrganizationDetailsResolverService]
    });
  });

  it('should be created', inject([OrganizationDetailsResolverService], (service: OrganizationDetailsResolverService) => {
    expect(service).toBeTruthy();
  }));
});
