import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from '../../models/user';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit {
  users: User[] = [];

  constructor(private userService: UserService, private router: Router) {}

  ngOnInit(): void {
    this.users = this.userService.getUsers();
  }

  navigateToCreate(): void {
    this.router.navigate(['/users/new']);
  }

  editUser(userId: number): void {
    this.router.navigate(['/users', userId]);
  }

  deleteUser(userId: number): void {
    this.userService.deleteUser(userId);
    this.users = this.userService.getUsers();
  }
}
