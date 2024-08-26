import {Component, OnInit} from '@angular/core';
import {
  TournamentDetailDto,
  TournamentDetailParticipantDto,
  TournamentStandingsDto,
  TournamentStandingsTreeDto
} from "../../../dto/tournament";
import {TournamentService} from "../../../service/tournament.service";
import {ActivatedRoute, Router} from "@angular/router";
import {NgForm} from "@angular/forms";
import {Location} from "@angular/common";
import {ToastrService} from "ngx-toastr";
import {ErrorFormatterService} from "../../../service/error-formatter.service";

@Component({
  selector: 'app-tournament-standings',
  templateUrl: './tournament-standings.component.html',
  styleUrls: ['./tournament-standings.component.scss']
})
export class TournamentStandingsComponent implements OnInit {
  standings: TournamentStandingsDto | undefined;


  public constructor(
    private service: TournamentService,
    private errorFormatter: ErrorFormatterService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
    private location: Location,
  ) {
  }

  public ngOnInit() {
      this.service.getById(Number(this.route.snapshot.paramMap.get('id'))).subscribe(
        {
          next: standings => {
            this.standings = standings;
          },
            error: error => {
              console.error('Error getting standings.', error);
              this.notification.error('Tournament could not be loaded', 'Error');
              this.router.navigate(['/tournaments']);
            }
          });
  }

  public submit(form: NgForm) {
    if(this.standings == undefined){
      return;
    }
    this.service.update(Number(this.route.snapshot.paramMap.get('id')), this.standings).subscribe({
      next: standings => {
        this.standings = standings;
        this.notification.success(`Tournament successfully updated.`);
        this.location.back();
      },
      error: error => {
        let notification = this.notification;
        console.error('Error getting standings.', error);
        if(error.status == 409 || error.status == 422){
          error.error.errors.forEach(function (value : string) {
            notification.error(error.error.errors);
          });
        }else{
          notification.error("Error getting standings.")
        }
      }
    });
  }

  public generateFirstRound() {
    if (!this.standings)
      return;
    // TODO implement
  }


  }
