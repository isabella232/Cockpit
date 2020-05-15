import React, { useState } from 'react';
import Button from '@material-ui/core/Button';
import ButtonGroup from '@material-ui/core/ButtonGroup';
import Grid from '@material-ui/core/Grid';
import { useParams } from 'react-router-dom';
import { useSelector } from 'react-redux';
import Obeya from '../Obeya/Obeya';
import Header from '../Header/Header';
import { mvpSelector } from '../../redux/selector';
import MvpCard from '../MvpCard/MvpCard';
import TeamCard from '../TeamCard/TeamCard';
import TechnologyCard from '../TechnologyCard/TechnologyCard';
import OverviewSprintTabs from '../OverviewSprintTabs/OverviewSprintTabs';
import useStyles from './styles';

function MvpInfoPage() {
  const classes = useStyles();
  const mvpId = useParams().id;
  const mvp = useSelector((state) => mvpSelector(state, mvpId));
  const [selectedTab, setSelectedTab] = useState('overview');
  return (
    <div>
      <Header />
      <Grid container className={classes.mvpInfoContainer}>
        <Grid item xs={3} className={classes.leftColumnContainer}>
          <MvpCard mvpInfo={mvp} />
          <TeamCard />
          <TechnologyCard />
        </Grid>

        <Grid item xs={9} className={classes.rightColumnContainer}>
          <Grid container alignItems="center">
            <Grid item xs={8}>
              <ButtonGroup color="primary">
                <Button
                  onClick={() => {
                    setSelectedTab('overview');
                  }}
                >
                  Overview
                </Button>
                <Button
                  onClick={() => {
                    setSelectedTab('sprint');
                  }}
                  disabled
                >
                  Sprint
                </Button>
              </ButtonGroup>
            </Grid>
            <Grid item xs={4}>
              <Obeya />
            </Grid>
            <OverviewSprintTabs selectedTab={selectedTab} />
          </Grid>
        </Grid>
      </Grid>
    </div>
  );
}

export default MvpInfoPage;