import { TestBed, inject } from '@angular/core/testing';

import { OrganizationTree2Service } from './organization-tree2.service';

describe('OrganizationTree2Service', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [OrganizationTree2Service]
    });
  });

  it('should be created', inject([OrganizationTree2Service], (service: OrganizationTree2Service) => {
    expect(service).toBeTruthy();
  }));
});
