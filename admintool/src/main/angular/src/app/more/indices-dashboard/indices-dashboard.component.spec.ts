import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { IndicesDashboardComponent } from './indices-dashboard.component';

describe('IndicesDashboardComponent', () => {
  let component: IndicesDashboardComponent;
  let fixture: ComponentFixture<IndicesDashboardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ IndicesDashboardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(IndicesDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
