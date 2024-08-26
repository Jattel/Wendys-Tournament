import {Component, OnInit} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {NgForOf, NgIf} from "@angular/common";
import {RouterLink} from "@angular/router";
import {Horse, HorseSearch, HorseSelection} from "../../../dto/horse";
import {Sex} from "../../../dto/sex";
import {
  TournamentDetailDto,
  TournamentDetailParticipantDto,
  TournamentListDto,
  TournamentSearchParams
} from "../../../dto/tournament";
import {debounceTime, Subject} from "rxjs";
import {ToastrService} from "ngx-toastr";
import {TournamentService} from "../../../service/tournament.service";

@Component({
  selector: 'app-tournament',
  standalone: true,
  imports: [
    FormsModule,
    NgForOf,
    NgIf,
    RouterLink
  ],
  templateUrl: './tournament.component.html',
  styleUrl: './tournament.component.scss'
})
export class TournamentComponent implements OnInit {
  search = false;
  tournaments: TournamentListDto[] = [];
  bannerError: string | null = null;
  searchParams: TournamentSearchParams = {};
  searchStartDate: string | null = null;
  searchEndDate: string | null = null;
  horseForDeletion: Horse | undefined;
  searchChangedObservable = new Subject<void>();

  constructor(
    private service: TournamentService,
    private notification: ToastrService,
  ) { }


  ngOnInit(): void {
    this.reloadTournaments();
    this.searchChangedObservable
      .pipe(debounceTime(300))
      .subscribe({next: () => this.reloadTournaments()});
  }

   reloadTournaments() {
    this.service.search(this.searchParams)
      .subscribe({
        next: data => {
          data.sort((a, b) => new Date(a.startDate).getTime() - new Date(b.startDate).getTime());
          this.tournaments = data;
        },
        error: error => {
          console.error('Error fetching tournaments', error);
          this.bannerError = 'Could not fetch tournaments: ' + error.message;
          const errorMessage = error.status === 0
            ? 'Is the backend up?'
            : error.message.message;
          this.notification.error(errorMessage, 'Could Not Fetch tournaments');
        }
      });
  }

  dateLocalFormat(date: Date): string {
    let formatted = new Date(date);
    return formatted.toLocaleDateString();
  }
}


