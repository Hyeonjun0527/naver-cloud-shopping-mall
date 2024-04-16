import './App.css';
// import { Fragment } from 'react';
import { Route, Switch } from 'react-router-dom/cjs/react-router-dom.min';
import Main from './main';

function App() {
  return (
    <Route>
        <div>
            {/* 네비게이션 바 컴포넌트 */}
            {/* <NavigationBar /> */}

            {/* 라우팅 설정*/}
            <Switch>
                <Route exact path="/" component={Main} />
            </Switch>
        </div>
    </Route>
  );
}

export default App;
