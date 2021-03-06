import React from 'react';
import { AuthConsumer } from '../../services/authProvider';

export const LogoutCallback = () => (
  <AuthConsumer>
    {({ signoutRedirectCallback }) => {
      signoutRedirectCallback();
      return <span>Logout callback</span>;
    }}
  </AuthConsumer>
);
