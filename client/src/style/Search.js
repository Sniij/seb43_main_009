import styled from 'styled-components';

export const SearchWrapper = styled.div`
  width: 100%;
  background-color: #ffffff;
`;

export const StyledInput = styled.input`
  width: 80%;
  padding: 12px 20px;
  margin: 8px 0;
  box-sizing: border-box;
  border: 2px solid #ffa1a1;
  border-radius: 4px;
`;

export const StyledButton = styled.button`
  width: 20%;
  background-color: #f05858;
  color: white;
  padding: 14px 20px;
  margin: 8px 0;
  border: none;
  border-radius: 4px;
  cursor: pointer;

  &:hover {
    background-color: #45a049;
  }
`;

export const StyledTable = styled.table`
  font-family: arial, sans-serif;
  border-collapse: collapse;
  width: 100%;

  td {
    border: 1px solid #000000;
    text-align: left;
    padding: 8px;
  }

  td:first-child {
    background-color: #f9e6e6;
    width: 40%;
  }
  td:nth-child(2) {
    width: 40%;
  }
  tr:first-child td:nth-child(3) {
    background-color: #f9e6e6;
  }
`;
