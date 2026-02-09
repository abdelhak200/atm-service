import { Component, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from '../../models/user';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit {
  users: User[] = [];
  filteredUsers: User[] = [];
  searchControl = new FormControl('');
  shareUrl = '';
  copyStatus = '';

  constructor(
    private userService: UserService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.users = this.userService.getUsers();
    const initialQuery = this.route.snapshot.queryParamMap.get('q') ?? '';
    this.searchControl.setValue(initialQuery, { emitEvent: false });
    this.applyFilter(initialQuery);

    this.searchControl.valueChanges.subscribe((value) => {
      const query = (value ?? '').toString();
      this.applyFilter(query);
      this.router.navigate([], {
        queryParams: { q: query.trim() || null },
        queryParamsHandling: 'merge',
        replaceUrl: true
      });
    });
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
    this.applyFilter(this.searchControl.value ?? '');
  }

  shareByEmail(): void {
    this.updateShareUrl();
    const subject = 'User Directory search results';
    const body = `Here is the link to the user search results: ${this.shareUrl}`;
    window.location.href = `mailto:?subject=${encodeURIComponent(subject)}&body=${encodeURIComponent(body)}`;
  }

  async copyShareLink(): Promise<void> {
    this.updateShareUrl();
    try {
      if (navigator.clipboard?.writeText) {
        await navigator.clipboard.writeText(this.shareUrl);
        this.copyStatus = 'Link copied to clipboard.';
      } else {
        this.copyStatus = 'Clipboard access is unavailable in this browser.';
      }
    } catch (error) {
      this.copyStatus = 'Unable to copy link. Please try again.';
    }
  }

  private applyFilter(query: string): void {
    const normalized = query.trim().toLowerCase();
    if (!normalized) {
      this.filteredUsers = [...this.users];
      this.updateShareUrl();
      return;
    }

    this.filteredUsers = this.users.filter((user) =>
      [
        user.id?.toString(),
        user.name,
        user.firstName,
        user.secondName,
        user.email,
        user.dateOfBirth
      ]
        .filter(Boolean)
        .some((value) => value!.toString().toLowerCase().includes(normalized))
    );
    this.updateShareUrl();
  }

  private updateShareUrl(): void {
    const query = (this.searchControl.value ?? '').toString().trim();
    const urlTree = this.router.createUrlTree([], {
      relativeTo: this.route,
      queryParams: { q: query || null },
      queryParamsHandling: 'merge'
    });
    this.shareUrl = `${window.location.origin}${this.router.serializeUrl(urlTree)}`;
  }
}
