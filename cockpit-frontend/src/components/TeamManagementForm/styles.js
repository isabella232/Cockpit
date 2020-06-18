import { makeStyles } from '@material-ui/core/styles';

export default makeStyles((theme) => ({
  paper: {
    height: 400,
    overflow: 'auto',
    width: 600,
    borderRadius: 0,
  },
  grid: {
    padding: theme.spacing(2, 4, 1),
    width: 'calc(80%)',
    margin: 0,
  },
  formLabel: {
    fontWeight: 'bold',
  },
  textField: {
    marginTop: 8,
    'margin-bottom': 8,
  },
  buttonSave: {
    borderRadius: 20,
    margin: 32,
  },
}));
