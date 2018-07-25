import { TestBed, inject } from '@angular/core/testing';

import { OrganizationTreeService } from './organization-tree.service';

describe('OrganizationTreeService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [OrganizationTreeService]
    });
  });

  it('should be created', inject([OrganizationTreeService], (service: OrganizationTreeService) => {
    expect(service).toBeTruthy();
  }));
});
