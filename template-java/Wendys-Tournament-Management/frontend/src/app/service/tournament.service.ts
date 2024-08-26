import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {map, Observable, tap, throwError} from 'rxjs';
import {formatIsoDate} from '../util/date-helper';
import {
  TournamentCreateDto, TournamentDetailDto, TournamentDetailParticipantDto,
  TournamentListDto,
  TournamentSearchParams,
  TournamentStandingsDto, TournamentStandingsTreeDto
} from "../dto/tournament";
import {Horse} from "../dto/horse";
const baseUri = environment.backendUrl + '/tournaments';

class ErrorDto {
  constructor(public message: String) {}
}

@Injectable({
  providedIn: 'root'
})
export class TournamentService {
  constructor(
    private http: HttpClient
  ) {
  }


  public create(tournament: TournamentCreateDto): Observable<TournamentCreateDto> {
    return this.http.post<TournamentCreateDto>(
      baseUri,
      tournament
    );
  }

   search(searchParams: TournamentSearchParams): Observable<TournamentListDto[]> {
    if (searchParams.name === '') {
      delete searchParams.name;
    }
    let params = new HttpParams();
    if (searchParams.name) {
      params = params.append('name', searchParams.name);
    }
    if(searchParams.startDate){
      params = params.append('startDate', formatIsoDate(searchParams.startDate));
    }
    if(searchParams.endDate){
      params = params.append('endDate', formatIsoDate(searchParams.endDate));
    }
    return this.http.get<TournamentListDto[]>(baseUri, { params })
      .pipe(tap(tournaments => tournaments.map(h => {
      })));
  }

  getById(id: number | null): Observable<TournamentStandingsDto> {
    console.log('Getting horse detail with the ID:' + id);
    return this.http.get<TournamentStandingsDto>(`${baseUri}/${id}`);
  }

  update(tournamentId: number, standings: TournamentStandingsDto): Observable<any> {
    console.log('Updating tournament with the ID:' + tournamentId);
    return this.http.put<any>(baseUri + '/' + tournamentId, standings);
  }
}
