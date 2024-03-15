import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginPhonenoComponent } from './login-phoneno.component';

describe('LoginPhonenoComponent', () => {
  let component: LoginPhonenoComponent;
  let fixture: ComponentFixture<LoginPhonenoComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LoginPhonenoComponent]
    });
    fixture = TestBed.createComponent(LoginPhonenoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
