import React from 'react';
import { Container} from 'react-bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';

const Main = () => {
  return (
    <div className="default-font">
      {/* ToolBar */}
      {/* <Toolbar /> */}
      {/* 위의 주석을 해제하고 Toolbar 컴포넌트를 구현해주세요 */}

      <Container className="p-5 bg-light rounded-3">
        <h1>Model2MVCShop</h1>
        <p>
          Welcome to Model2MVCShop! We offer a wide range of products and services.
        </p>
        {/* 추가적인 내용을 여기에 작성하세요 */}
      </Container>

      <Container>
        {/* 추가적인 내용을 여기에 작성하세요 */}
      </Container>
    </div>
  );
};

export default Main;