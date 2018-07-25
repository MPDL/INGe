import { TestBed, inject } from '@angular/core/testing';

import { UserDetailsResolverService } from './user-details-resolver.service';

describe('UserDetailsResolverService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [UserDetailsResolverService]
    });
  });

  it('should be created', inject([UserDetailsResolverService], (service: UserDetailsResolverService) => {
    expect(service).toBeTruthy();
  }));
});
