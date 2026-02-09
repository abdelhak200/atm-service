import { Injectable } from '@angular/core';
import { User } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private users: User[] = [
    {
      id: 1001,
      name: 'Anderson',
      firstName: 'Maya',
      secondName: 'Louise',
      dateOfBirth: '1990-04-12',
      email: 'maya.anderson@example.com'
    },
    {
      id: 1002,
      name: 'Taylor',
      firstName: 'Ethan',
      secondName: 'James',
      dateOfBirth: '1988-09-30',
      email: 'ethan.taylor@example.com'
    },
    {
      id: 1003,
      name: 'Nguyen',
      firstName: 'Linh',
      secondName: 'Mai',
      dateOfBirth: '1995-01-22',
      email: 'linh.nguyen@example.com'
    }
  ];

  getUsers(): User[] {
    return [...this.users];
  }

  getUser(id: number): User | undefined {
    return this.users.find((user) => user.id === id);
  }

  addUser(user: User): void {
    this.users = [...this.users, user];
  }

  updateUser(updatedUser: User): void {
    this.users = this.users.map((user) => (user.id === updatedUser.id ? updatedUser : user));
  }

  deleteUser(id: number): void {
    this.users = this.users.filter((user) => user.id !== id);
  }
}
