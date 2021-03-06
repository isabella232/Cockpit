import React, { useEffect } from 'react';
import TextField from '@material-ui/core/TextField';
import Grid from '@material-ui/core/Grid';
import FormLabel from '@material-ui/core/FormLabel';
import MenuItem from '@material-ui/core/MenuItem';
import Select from '@material-ui/core/Select';
import Paper from '@material-ui/core/Paper';
import FormControl from '@material-ui/core/FormControl';
import { useParams } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { mvpSelector } from '../../redux/selector';
import {
  setName,
  setPitch,
  setEntity,
  setScopeCommitment,
  setStatus,
  setImageUrl,
  setMvpStartDate,
  setMvpEndDate,
  nameState,
  pitchState,
  entityState,
  scopeCommitmentState,
  sprintNumberState,
  statusState,
  imageUrlState,
  setSprintNumber,
} from './InformationFormSlice';
import useStyles from './styles';

export default function InformationForm() {
  const classes = useStyles();
  const dispatch = useDispatch();
  const mvpId = useParams().id;
  const mvpInfo = useSelector((state) => mvpSelector(state, mvpId));

  const name = useSelector(nameState);
  const pitch = useSelector(pitchState);
  const scopeCommitment = useSelector(scopeCommitmentState);
  const nbSprint = useSelector(sprintNumberState);
  const entity = useSelector(entityState);
  const status = useSelector(statusState);
  const urlMvpAvatar = useSelector(imageUrlState);

  useEffect(() => {
    dispatch(setName(mvpInfo.name));
    dispatch(setPitch(mvpInfo.mvpDescription));
    dispatch(setStatus(mvpInfo.status));
    dispatch(setEntity(mvpInfo.entity));
    dispatch(setScopeCommitment(mvpInfo.scopeCommitment));
    dispatch(setSprintNumber(mvpInfo.sprintNumber));
    dispatch(setImageUrl(mvpInfo.urlMvpAvatar));
    dispatch(setMvpStartDate(mvpInfo.jira.mvpStartDate));
    dispatch(setMvpEndDate(mvpInfo.jira.mvpEndDate));
  }, [dispatch, mvpInfo]);
  function isImageUrlValid(urlImage) {
    return (
      urlImage !== null &&
      (urlImage.startsWith('http://') || urlImage.startsWith('https://'))
    );
  }
  function handleNameChange(event) {
    dispatch(setName(event.target.value));
  }
  function handlePitchChange(event) {
    dispatch(setPitch(event.target.value));
  }
  function handleScopeCommitmentChange(event) {
    dispatch(setScopeCommitment(event.target.value));
  }
  function handleSprintNumberChange(event) {
    dispatch(setSprintNumber(event.target.value));
  }
  function handleStatusChange(event) {
    dispatch(setStatus(event.target.value));
  }
  function handleEntityChange(event) {
    dispatch(setEntity(event.target.value));
  }
  function handleImageChange(event) {
    dispatch(setImageUrl(event.target.value));
  }

  return (
    <Paper className={classes.paper}>
      <form>
        <Grid className={classes.grid} container spacing={1}>
          <Grid item xs={12}>
            <FormLabel className={classes.formLabel}>Name</FormLabel>
            <TextField
              className={classes.textField}
              required
              fullWidth
              value={name}
              variant="outlined"
              id="mvpName"
              name="name"
              placeholder="MVP Name"
              size="small"
              onChange={handleNameChange}
              inputProps={{ maxLength: 30 }}
            />
          </Grid>

          <Grid item xs={12}>
            <FormLabel className={classes.formLabel}>
              Scope commitment
            </FormLabel>
            <TextField
              className={classes.textField}
              value={scopeCommitment}
              required
              fullWidth
              variant="outlined"
              id="scopeCommitment"
              name="scopeCommitment"
              placeholder="Scope commitment"
              size="small"
              type="number"
              inputProps={{ min: '0', step: '1' }}
              onChange={handleScopeCommitmentChange}
            />
          </Grid>
          <Grid item xs={12}>
            <FormLabel className={classes.formLabel}>Status</FormLabel>
            <FormControl
              required
              size="small"
              fullWidth
              variant="outlined"
              className={classes.textField}
              value={mvpInfo.status}
            >
              <Select
                displayEmpty
                value={status || ''}
                onChange={handleStatusChange}
              >
                <MenuItem value="condidates" disabled>
                  Condidates
                </MenuItem>
                <MenuItem value="inprogress">In progress</MenuItem>
                <MenuItem value="transferred">Transferred</MenuItem>
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={12}>
            <FormLabel className={classes.formLabel}>Entity</FormLabel>
            <FormControl
              required
              size="small"
              fullWidth
              variant="outlined"
              className={classes.textField}
            >
              <Select
                displayEmpty
                value={entity || ''}
                onChange={handleEntityChange}
              >
                <MenuItem value="EP">EP</MenuItem>
                <MenuItem value="RC">RC</MenuItem>
                <MenuItem value="MS">MS</MenuItem>
                <MenuItem value="GRP">GRP</MenuItem>
                <MenuItem value="TDF">TDF</MenuItem>
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={12}>
            <div style={{ display: 'flex' }}>
              <div>
                <FormLabel className={classes.formLabel}>
                  MVP photo URL
                </FormLabel>
                <TextField
                  className={classes.textField}
                  value={urlMvpAvatar}
                  required
                  fullWidth
                  variant="outlined"
                  id="mvpPhoto"
                  name="mvpPhoto"
                  placeholder="https://..."
                  size="small"
                  onChange={handleImageChange}
                  error={!isImageUrlValid(urlMvpAvatar)}
                  helperText={
                    isImageUrlValid(urlMvpAvatar)
                      ? ''
                      : 'Url starts with http(s)://'
                  }
                />
              </div>
              <img className={classes.imgStyle} src={urlMvpAvatar} alt="img" />
            </div>
          </Grid>
          <Grid item xs={12}>
            <FormLabel className={classes.formLabel}>Pitch</FormLabel>
            <TextField
              className={classes.textField}
              value={pitch || ''}
              required
              fullWidth
              variant="outlined"
              id="pitch"
              name="pitch"
              placeholder="Pitch"
              size="small"
              multiline
              rows="3"
              onChange={handlePitchChange}
              inputProps={{ maxLength: 500 }}
            />
          </Grid>
          <Grid item xs={12}>
            <FormLabel className={classes.formLabel}>Sprint number</FormLabel>
            <TextField
              className={classes.textField}
              value={
                nbSprint >= mvpInfo.jira.currentSprint
                  ? nbSprint
                  : mvpInfo.jira.currentSprint
              }
              required
              fullWidth
              variant="outlined"
              id="sprintNumber"
              name="sprintNumber"
              placeholder="Sprint number"
              size="small"
              type="number"
              inputProps={{ min: '1', step: '1' }}
              onChange={handleSprintNumberChange}
              error={nbSprint < mvpInfo.jira.currentSprint}
              helperText={
                // eslint-disable-next-line no-nested-ternary
                nbSprint < mvpInfo.jira.currentSprint
                  ? 'Sprint Number must be greater than or equal to Current Sprint'
                  : ''
              }
            />
          </Grid>
        </Grid>
      </form>
    </Paper>
  );
}
