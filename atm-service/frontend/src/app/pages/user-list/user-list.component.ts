import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from '../../models/user';
import { UserService } from '../../services/user.service';

type FilterField = keyof Pick<User, 'id' | 'name' | 'firstName' | 'secondName' | 'dateOfBirth' | 'email'>;

type ColumnKey = FilterField | 'actions';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit, OnDestroy {
  users: User[] = [];
  filteredUsers: User[] = [];

  filters: Record<FilterField, string> = {
    id: '',
    name: '',
    firstName: '',
    secondName: '',
    dateOfBirth: '',
    email: ''
  };

  readonly columnWidths: Record<ColumnKey, number> = {
    id: 90,
    name: 170,
    firstName: 170,
    secondName: 170,
    dateOfBirth: 150,
    email: 230,
    actions: 220
  };

  private resizingColumn: ColumnKey | null = null;
  private startX = 0;
  private startWidth = 0;

  private readonly onMouseMove = (event: MouseEvent): void => {
    if (!this.resizingColumn) {
      return;
    }

    const nextWidth = this.startWidth + (event.clientX - this.startX);
    this.columnWidths[this.resizingColumn] = Math.max(80, nextWidth);
  };

  private readonly onMouseUp = (): void => {
    this.stopResizing();
  };

  constructor(private userService: UserService, private router: Router) {}

  ngOnInit(): void {
    this.users = this.userService.getUsers();
    this.applyFilters();
  }

  ngOnDestroy(): void {
    this.stopResizing();
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
    this.applyFilters();
  }

  onFilterChange(field: FilterField, event: Event): void {
    const value = (event.target as HTMLInputElement).value.trim();
    this.filters[field] = value;
    this.applyFilters();
  }

  startResizing(event: MouseEvent, column: ColumnKey): void {
    event.preventDefault();

    this.resizingColumn = column;
    this.startX = event.clientX;
    this.startWidth = this.columnWidths[column];

    document.addEventListener('mousemove', this.onMouseMove);
    document.addEventListener('mouseup', this.onMouseUp);
  }

  private stopResizing(): void {
    if (!this.resizingColumn) {
      return;
    }

    this.resizingColumn = null;
    document.removeEventListener('mousemove', this.onMouseMove);
    document.removeEventListener('mouseup', this.onMouseUp);
  }

  private applyFilters(): void {
    this.filteredUsers = this.users.filter((user) => {
      const idText = user.id.toString().toLowerCase();

      return (
        idText.includes(this.filters.id.toLowerCase()) &&
        user.name.toLowerCase().includes(this.filters.name.toLowerCase()) &&
        user.firstName.toLowerCase().includes(this.filters.firstName.toLowerCase()) &&
        user.secondName.toLowerCase().includes(this.filters.secondName.toLowerCase()) &&
        user.dateOfBirth.toLowerCase().includes(this.filters.dateOfBirth.toLowerCase()) &&
        user.email.toLowerCase().includes(this.filters.email.toLowerCase())
      );
    });
  }
}
