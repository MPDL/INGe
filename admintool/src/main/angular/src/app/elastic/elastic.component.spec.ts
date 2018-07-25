import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ElasticComponent } from './elastic.component';

describe('ElasticComponent', () => {
  let component: ElasticComponent;
  let fixture: ComponentFixture<ElasticComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ElasticComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ElasticComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
