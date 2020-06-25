import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ManageListComponent} from './manage-list.component';
import {CssHelperService} from "../service/css-helper.service";

xdescribe('ManageListComponent', () => {
  let component: ManageListComponent;
  let fixture: ComponentFixture<ManageListComponent>;

  beforeEach(async(() => {
    const cssHelper = jasmine.createSpyObj('CssHelperService', ['foo']);
    TestBed.configureTestingModule({
      providers: [
        ManageListComponent,
        {provide: CssHelperService, useValue: cssHelper}
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ManageListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
