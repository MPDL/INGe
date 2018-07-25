import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConeListComponent } from './cone-list.component';

describe('ConeListComponent', () => {
  let component: ConeListComponent;
  let fixture: ComponentFixture<ConeListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConeListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConeListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
