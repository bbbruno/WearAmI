#define PORT        8888
#define PORT_INT        8889
/* REPLACE with your server machine name*/
#define HOST        "127.0.0.1"//"130.251.13.73"//
#define HOST_INT        "127.0.0.1"
#define DIRSIZE     8192

void ClassifyOnline() {

	char hostnameS[100];
	char dirS[DIRSIZE];
	int sdS;
	struct sockaddr_in sinS;
	struct sockaddr_in pinS, cli_addrS;
	struct hostent *hpS;
	int clilenS, newsockfdS;

	strcpy(hostnameS, HOST_INT);

	if ((hpS = gethostbyname(hostnameS)) == 0) {
		perror("gethostbyname");
		exit(1);
	}

	memset(&pinS, 0, sizeof(pinS));
	pinS.sin_family = AF_INET;
	pinS.sin_addr.s_addr = ((struct in_addr *) (hpS->h_addr))->s_addr;
	pinS.sin_port = htons(PORT_INT);

	if ((sdS = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
		perror("socket");
		exit(1);
	}

	fcntl(sdS, F_SETFL, O_NDELAY);

	while (bind(sdS, (struct sockaddr *) &pinS, sizeof(pinS)) == -1) {
		perror("connecto");
		//exit(1);
	}

	char hostname[100];
	char dir[DIRSIZE];
	int sd;
	struct sockaddr_in sin;
	struct sockaddr_in pin;
	struct hostent *hp;

	strcpy(hostname, HOST);


	if ((hp = gethostbyname(hostname)) == 0) {
		perror("gethostbyname");
		exit(1);
	}


	memset(&pin, 0, sizeof(pin));
	pin.sin_family = AF_INET;
	pin.sin_addr.s_addr = ((struct in_addr *) (hp->h_addr))->s_addr;
	pin.sin_port = htons(PORT);


	if ((sd = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
		perror("socket");
		exit(1);
	}


	if (connect(sd, (struct sockaddr *) &pin, sizeof(pin)) == -1) {
		perror("connect");
		exit(1);
	}

	cout << "Modelli" << endl;
	mat bpDrink = ReadNewPModel("gmmDrink/gmr_MuBod.txt");
	mat gpDrink = ReadNewPModel("gmmDrink/gmr_MuGrav.txt");
	cube bsDrink = ReadNewSModel("gmmDrink/gmr_SigmaBod.txt");
	cube gsDrink = ReadNewSModel("gmmDrink/gmr_SigmaGrav.txt");

	mat bpWalk = ReadNewPModel("gmmWalk/gmr_MuBod.txt");
	mat gpWalk = ReadNewPModel("gmmWalk/gmr_MuGrav.txt");
	cube bsWalk = ReadNewSModel("gmmWalk/gmr_SigmaBod.txt");
	cube gsWalk = ReadNewSModel("gmmWalk/gmr_SigmaGrav.txt");

	int win_size;
	if (bpWalk.n_cols > bpDrink.n_cols)
		win_size = bpWalk.n_cols + delay; // Dimensione finestra
	else
		win_size = bpDrink.n_cols + delay;
	cout << bpWalk.n_cols << " " << bpDrink.n_cols << " " << win_size << endl;
	int nSamples = 0; // Campioni inseriti nella finestra

	//ACQUSISCO I DATI
	int x, y, z;

	cout << "Inzio" << endl;
	mat actsample;
	mat window = zeros<mat>(win_size, 3);
	mat gravity = zeros<mat>(win_size, 3);
	mat body = zeros<mat>(win_size, 3);

	FILE *pf = fopen("ondata.txt", "w");
	while (1) {

		/*Check new connection*/
		listen(sdS, 5);
		clilenS = sizeof(cli_addrS);

		int tempso;
		tempso = accept(sdS, (struct sockaddr *) &cli_addrS, &clilenS);
		if (tempso != -1) {
			newsockfdS = tempso;
			char buf[16];
			memset(buf, 0, 16);
			inet_ntop(AF_INET, &(cli_addrS.sin_addr), buf, 16 - 1);
			printf(
					"<CommExpert::CheckNewConnections()> Found new connection from %s\n",
					buf);

		}

		/*****************************/

		int letti;
		/* wait for a message to come back from the server */
		if (recv(sd, dir, 9, 0) == -1) {
			perror("recv");
			exit(1);
		}
		char c = '_';
		char cnum[2];
		int value[] = { 0, 0, 0 };
		int ind = 0, ival = 0;
		//printf("STRING: %s\n",dir);fflush(stdout);
		while (ival < 3) {
			//printf("%c",c);fflush(stdout);
			c = dir[ind];
			//printf("%c- %s\n",c, cnum);fflush(stdout);
			if (c == ' ' || c == '\n') {

				value[ival] = atoi(cnum);
				cnum[0] = '0';
				cnum[1] = '0';
				ival++;

			} else {
				cnum[0] = cnum[1];
				cnum[1] = c;
			}
			ind++;
		}

		/* spew-out the results and bail out of here! */
		//printf("%d %d %d\n", value[0], value[1], value[2]);
		fflush (stdout);

		actsample << value[0] << value[1] << value[2];
		fprintf(pf, "%d %d %d\n", value[0], value[1], value[2]);
		fflush(pf);

		//cout<<"Sample: "<<actsample<<" End"<<endl;;
		//CREO LA FINESTRA
		CreateWindow(actsample, window, win_size, nSamples);
		//cout<<window.row(0)<<endl;
		if (nSamples >= win_size) {
			//cout<<"Analizzo"<<endl;
			AnalyzeActualWindow(window, nSamples, gravity, body, false);
			//cout<<body(0,2)<<endl;
			//cout<<"Resize"<<endl;
			gravity = gravity.rows(0, gravity.n_rows - delay - 1);
			//cout<<gravity;
			//return 0;
			body = body.rows(0, body.n_rows - delay - 1);
			//cout<<"Comparo"<<endl;
			mat compareGr = gravity.rows(0, bpDrink.n_cols);
			mat compareBo = body.rows(0, bpDrink.n_cols);
			//cout<<"Comparo"<<endl;
			float likeDrink = v3D_CompareWithModels(compareGr, compareBo,
					gpDrink, gsDrink, bpDrink, bsDrink);
			compareGr = gravity.rows(0, bpWalk.n_cols - 1);
			compareBo = body.rows(0, bpWalk.n_cols - 1);
			float likeWalk = v3D_CompareWithModels(compareGr, compareBo, gpWalk,
					gsWalk, bpWalk, bsWalk);

			if(likeDrink<70)
				cout<< "BERE"<<endl;
			else
				if(likeWalk<25)
					cout<<"CAMMINA"<<endl;
				else
					cout << likeDrink << " " << likeWalk << endl;

			char strtosend[50];
			sprintf(strtosend, "%d#%d#%d#%d#%d\n", value[0], value[1], value[2], ((int) likeDrink),((int) likeWalk));
			send(newsockfdS, strtosend, strlen(strtosend), 0);
			//usleep(1000000 / 32);
		}

	}
	close(sd);
	fclose(pf);
}
