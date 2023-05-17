import Layout from '../../common/Layout';
import React, { useState } from 'react';
import axios from 'axios';
import {
  SearchWrapper,
  StyledInput,
  StyledButton,
  StyledTable,
} from '../../style/Search';

import {
  SBackgroundLayout,
  SGradiant,
  STextInfoSection,
} from '../../style/GreetingInfo';

const SearchMain = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [data, setData] = useState([]);

  const dummy = [
    {
      itemName: '정보없음',
      entpName: '정보없음',
      efcyQesitm: '정보없음',
      useMethodQesitm: '정보없음',
      atpnQesitm: '정보없음',
      intrcQesitm: '정보없음',
      itemImage: '정보없음',
    },
  ];
  const handleSearch = async () => {
    try {
      const response = await axios.get(
        `https://server.dowajoyak.shop/search?itemName=${searchTerm}`,
      );
      if (typeof response.data === 'object') {
        setData(response.data);
      } else setData(dummy);
    } catch (error) {
      console.error(error);
    }
  };
  return (
    <Layout>
      <SBackgroundLayout>
        <SGradiant />
        <STextInfoSection>
          <div className="pills">💊알고싶은 약의 성분을 확인해보세요!</div>
          <h3>가지고 계신 🤧알러지를 표기해드립니다</h3>
          <StyledInput
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="🔎 게보린, 아스피린"
          />
          <div className="example">
            아직 알러지를 설정하지 않으셨다면, 맞춤 추천 후 검색을 추천드립니다
          </div>
        </STextInfoSection>
      </SBackgroundLayout>
    </Layout>
  );
};

export default SearchMain;
