import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ElasticStartComponent } from './elastic-start.component';

describe('StartComponent', () => {
  let component: ElasticStartComponent;
  let fixture: ComponentFixture<ElasticStartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ElasticStartComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ElasticStartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
