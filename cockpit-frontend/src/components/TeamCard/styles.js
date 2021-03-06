import { makeStyles } from '@material-ui/core/styles';

export default makeStyles(() => ({
  teamCard: {
    marginTop: '20px',
    backgroundColor: '#F5F5F9',
  },
  teamName: {
    fontWeight: 'bold',
  },
  capitalizedText: {
    textTransform: 'capitalize',
  },
  inline: {
    display: 'inline',
    color: 'gray',
  },
  listItem: {
    paddingTop: 0,
    paddingBottom: 0,
    paddingLeft: 0,
  },
  uppercaseText: {
    textTransform: 'uppercase',
  },
}));
