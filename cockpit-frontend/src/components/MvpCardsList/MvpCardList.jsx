import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import Grid from '@material-ui/core/Grid';
import { Link } from 'react-router-dom';
import DeleteIcon from '@material-ui/icons/Delete';
import IconButton from '@material-ui/core/IconButton';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import Button from '@material-ui/core/Button';
import MvpCard from '../MvpCard/MvpCard';
import { fetchAllMvps, deleteMvp } from '../../redux/ormSlice';
import { mvpSelector } from '../../redux/selector';
import { isScrumMasterState } from '../HomePage/HomePageSlice';
import { selectFilterState } from '../MvpEntityFilter/mvpEntityFilterSlice';

// styles
import useStyles from './styles';

export default function MvpCardList(props) {
  const classes = useStyles();
  const dispatch = useDispatch();
  const isScrumMaster = useSelector(isScrumMasterState);
  const selectedEntity = useSelector(selectFilterState);
  const [openDialog, setOpenDialog] = React.useState(false);
  const [selectedMvp, setSelectedMvp] = React.useState({});

  useEffect(() => {
    dispatch(fetchAllMvps());
  }, [dispatch]);

  // Filter Mvps with selected mvp status
  let mvpList = useSelector((state) => mvpSelector(state)).filter(
    (mvp) => mvp.status === props.mvpState,
  );
  if (selectedEntity !== 'ALL ENTITIES') {
    mvpList = mvpList.filter((mvp) => mvp.entity === selectedEntity);
  }

  function deleteJira(e, id) {
    e.stopPropagation();
    dispatch(deleteMvp(id));
    setOpenDialog(false);
  }
  const handleCloseDialog = () => {
    setOpenDialog(false);
  };
  const handleClickOpen = (mvp) => {
    setOpenDialog(true);
    setSelectedMvp(mvp);
  };

  return (
    <Grid container>
      <Grid item xs={12}>
        <Grid container spacing={5} className={classes.gridList}>
          {mvpList.map((mvp) => (
            <Grid item key={mvp.id}>
              {isScrumMaster && (
                <IconButton
                  className={classes.deleteButton}
                  aria-label="delete"
                  color="secondary"
                  onClick={() => handleClickOpen(mvp)}
                >
                  <DeleteIcon />
                </IconButton>
              )}
              <Link
                to={`/mvp-info/${mvp.id}`}
                className={classes.cardRouterLink}
              >
                <MvpCard mvpInfo={mvp} />
              </Link>
            </Grid>
          ))}
        </Grid>
      </Grid>
      <Dialog open={openDialog} onClose={handleCloseDialog}>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to delete this mvp definitely?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button
            onClick={(e) => deleteJira(e, selectedMvp.jira.id)}
            color="primary"
          >
            Yes
          </Button>
          <Button onClick={handleCloseDialog} color="primary">
            No
          </Button>
        </DialogActions>
      </Dialog>
    </Grid>
  );
}
