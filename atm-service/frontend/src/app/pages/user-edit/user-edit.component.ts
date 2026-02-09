import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from '../../models/user';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.css']
})
export class UserEditComponent implements OnInit {
  isNew = true;

  userForm = this.formBuilder.group({
    id: [0, [Validators.required, Validators.min(1)]],
    name: ['', [Validators.required]],
    firstName: ['', [Validators.required]],
    secondName: ['', [Validators.required]],
    dateOfBirth: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]]
  });

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const userId = Number(idParam);
      const user = this.userService.getUser(userId);
      if (user) {
        this.isNew = false;
        this.userForm.patchValue(user);
      }
    }
  }

  saveUser(): void {
    if (this.userForm.invalid) {
      this.userForm.markAllAsTouched();
      return;
    }

    const user = this.userForm.getRawValue() as User;
    if (this.isNew) {
      this.userService.addUser(user);
    } else {
      this.userService.updateUser(user);
    }

    this.router.navigate(['/users']);
  }

  cancel(): void {
    this.router.navigate(['/users']);
  }
}
