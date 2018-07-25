import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OrganizationSearchComponent } from './organization-search.component';

describe('OrganizationSearchComponent', () => {
  let component: OrganizationSearchComponent;
  let fixture: ComponentFixture<OrganizationSearchComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OrganizationSearchComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OrganizationSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
